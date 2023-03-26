package com.global.code.service

import com.global.code.repository.userRepository
import com.global.code.model.users

class userService(private val userRepo: userRepository) {
	
    fun getUserById(id: Long): users {
        /*
        "findById" gives you the data which id(Primary Key) value is the same as the argument

        In SQL, SELECT * FROM TABLE_NAME WHERE id = ?(value of the argument)

        "orElseThrow" is for the situation when there is no data with the same id as the argument
        and it returns with a message of "String is empty"
        */
        return userRepo.findById(id).orElseThrow { NoSuchElementException() }
    }

    fun createUser(user: users): users {
        /*
        "save" gives you the data which id(Primary Key) value is the same as the argument
        PK, id, will be auto generated because of "@GeneratedValue(strategy = GenerationType.IDENTITY)" in users class

        In SQL, INSERT INTO TABLE_NAME(every other field except PK) VALUES(respective Property names of the Entity)
        */
        return userRepo.save(user)
    }

    fun updateUserById(id: Long, user: users): users {
        /*
        It searches with the PK because it only wants to update one specific line of data
        When it found that data, it replaces the respective fields with the new data
        I'm not very sure but in this case, "save" serves as UPDATE because oldUser has/knows PK value
        and in "createUser" method, "id" property of the "user" object is 0, meaning that it's a new data

        In SQL, UPDATE TABLE_NAME SET FIELDS=?(respective data) WHERE id=?(oldUser.id)
        */
        val oldUser = userRepo.findById(id).orElseThrow { NoSuchElementException() }
        oldUser.name = user.name
        oldUser.email = user.email
		
        return userRepo.save(oldUser)
    }

    fun deleteUserById(id: Long) {
        /*
        "deleteById" deletes a specific data line because it deletes with PK
        We also can make this method delete with other fields(e.g. "deleteByEmail")
        Searching by fields can be done with the same manner
        Search all => findAll()

        In SQL, DELETE FROM TABLE_NAME WHERE id=?(value of the argument)
        */
        userRepo.deleteById(id)
    }
	
	fun getUserByEmail(email: String): users? {
        return userRepo.findByEmail(email)
    }
}